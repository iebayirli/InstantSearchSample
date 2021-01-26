package com.huawei.instantsearchsample.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.huawei.hms.searchkit.SearchKitInstance
import com.huawei.hms.searchkit.bean.WebItem
import com.huawei.hms.searchkit.bean.WebSearchRequest
import com.huawei.hms.searchkit.utils.Language
import com.huawei.hms.searchkit.utils.Region
import com.huawei.instantsearchsample.R
import com.huawei.instantsearchsample.databinding.ActivityMainBinding
import com.huawei.instantsearchsample.rest.QueryService
import com.huawei.instantsearchsample.ui.adapter.SearchAdapter
import com.huawei.instantsearchsample.ui.listener.IUrlClickListener
import com.huawei.instantsearchsample.util.ApplicationConstants
import com.huawei.instantsearchsample.util.ProgressDialogScreen
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.koin.android.ext.android.inject
import org.koin.android.scope.lifecycleScope
import kotlin.coroutines.CoroutineContext

@ExperimentalCoroutinesApi
class MainActivity : AppCompatActivity(), CoroutineScope, IUrlClickListener {

    private val job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    private val queryService by inject<QueryService>()

    private lateinit var binding: ActivityMainBinding

    private val progressDialog by lazy {
        ProgressDialogScreen(this)
    }

    private val adapter by lazy {
        SearchAdapter<WebItem>(R.layout.item_web_search, listOf(), this)
    }

    private val webSearchRequest by lazy {
        WebSearchRequest().apply {
            lang = Language.TURKISH
            sregion = Region.TURKEY
            pn = 1
            ps = 10
            within = "www.hepsiburada.com"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        SearchKitInstance.enableLog()
        SearchKitInstance.init(this, ApplicationConstants.appId)

        binding.rvResults.adapter = adapter

        initAccessToken()
        initListener()

    }

    private fun initAccessToken() {
        progressDialog.showProgress("Getting access token...")
        launch {
            val response = queryService.getAccessToken(
                "client_credentials",
                ApplicationConstants.clientId,
                ApplicationConstants.clientSecret
            )
            withContext(Dispatchers.Main) {
                response.accessToken?.apply {
                    //SearchKitInstance.getInstance().setInstanceCredential(this)
                    SearchKitInstance.getInstance().webSearcher.setCredential(this)
                    progressDialog.dismissProgress()
                } ?: run {
                    progressDialog.dismissProgress()
                    Toast.makeText(this@MainActivity, "Token returned null.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun initListener() {
        launch {
            binding.edtQuery.getQueryTextChangeStateFlow()
                .debounce(300)
                .filter { query ->
                    if (query.isEmpty()) {
                        withContext(Dispatchers.Main) {
                            adapter.updateData(listOf())
                        }
                        return@filter false
                    } else {
                        return@filter true
                    }
                }.distinctUntilChanged()
                .flatMapLatest { query ->
                    binding.progressBar.setVisible()
                    makeWebSearch(query)
                        .catch { emitAll(flowOf(listOf())) }
                }
                .collect { list ->
                    binding.progressBar.setInvisible {
                        adapter.updateData(list)
                    }
                }
        }
    }

    private fun makeWebSearch(query: String): Flow<List<WebItem>> {
        webSearchRequest.q = query
        return flow {
            val response =
                SearchKitInstance.getInstance().webSearcher.search(webSearchRequest)
            if (response != null)
                emit(response.data)
        }
    }

    private fun TextInputEditText.getQueryTextChangeStateFlow(): StateFlow<String> {
        val query = MutableStateFlow("")

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // beforeTextChanged
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                query.value = p0.toString()
            }

            override fun afterTextChanged(p0: Editable?) {
                // afterTextChanged
            }
        })
        return query
    }

    override fun onUrlClick(url: String) {
        startActivity(
            Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(url)
            }
        )
    }

    private suspend fun ProgressBar.setVisible() {
        withContext(Dispatchers.Main) {
            if (!this@setVisible.isAnimating)
                visibility = View.VISIBLE

        }
    }

    private suspend fun ProgressBar.setInvisible(scope: (() -> Unit)? = null) {
        withContext(Dispatchers.Main) {
            if (this@setInvisible.isAnimating) {
                visibility = View.GONE
                scope?.invoke()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        job.cancel()
    }

}