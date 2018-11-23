package com.glink.inspect

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v7.widget.AppCompatButton
import android.text.TextUtils
import android.view.MenuItem
import com.glink.R
import com.glink.inspect.base.BaseResponse
import com.glink.inspect.http.BaseObserver
import com.glink.inspect.http.HttpRequest
import com.glink.inspect.utils.CommonUtil
import com.glink.inspect.utils.ToastUtils
import retrofit2.HttpException

/**
 * @author jiangshuyang
 */
class SettingActivity : UrlBaseActivity() {

    lateinit var ipEt: TextInputEditText
    lateinit var ipLayout: TextInputLayout
    lateinit var portEt: TextInputEditText
    lateinit var confirmBtn: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        ipEt = findViewById(R.id.et_ip)
        ipLayout = findViewById(R.id.til_ip)
        portEt = findViewById(R.id.et_port)
        confirmBtn = findViewById(R.id.btn_confirm)
        confirmBtn.setOnClickListener({
            val ipStr = ipEt.text.toString()
            val portStr = portEt.text.toString()
            if (TextUtils.isEmpty(ipStr)) {
                ipLayout.error = getString(R.string.http_error_null)

            } else {
                val url = CommonUtil.getPingUrl(ipStr, portStr)
                if (url != null) {
                    verifyUrl(ipStr, portStr)
                } else {
                    ipLayout.error = getString(R.string.http_error_format)
                    ipLayout.editText!!.isFocusable = true
                    ipLayout.editText!!.isFocusableInTouchMode = true
                    ipLayout.editText!!.requestFocus()
                }
            }
        })

        val supportActionBar = supportActionBar
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar.setDisplayHomeAsUpEnabled(true)
        supportActionBar.setTitle("")

        if (!TextUtils.isEmpty(requestIP)) {
            ipEt.setText(requestIP)
        }
        if (!TextUtils.isEmpty(requestPort)) {
            portEt.setText(requestPort)
        }
        ipEt.setSelection(ipEt.text.toString().length)
    }


    private fun verifyUrl(ip: String, port: String) {
        HttpRequest.verifyUrl(this, ip, port, object : BaseObserver<BaseResponse<*>>(this) {
            override fun onNext(baseResponse: BaseResponse<*>) {
                super.onNext(baseResponse)
                setDataCache(ip, port)
            }

            override fun onComplete() {
                super.onComplete()
                setResult(Activity.RESULT_OK)
                finish()
            }

            override fun onError(e: Throwable) {
                super.onError(e)
                if (e is HttpException) {
                    //HTTP错误
                    ToastUtils.showMsg(getContext(), getString(R.string.http_error_pre) + e.code())
                } else {
                    ToastUtils.showMsg(getContext(), getString(R.string.http_error_exception))
                }
            }
        })
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            android.R.id.home -> onBackPressed()
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        super.onBackPressed()
    }

    companion object {
        fun newIntent(fromActivity: Activity): Intent {
            return Intent(fromActivity, SettingActivity::class.java)
        }
    }
}
