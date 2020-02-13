package ru.ttmf.mark.settings

import android.os.Bundle
import android.view.View
import android.webkit.URLUtil
import kotlinx.android.synthetic.main.fragment_settings.*
import okhttp3.TlsVersion.*
import ru.ttmf.mark.R
import ru.ttmf.mark.common.BaseFragment
import ru.ttmf.mark.network.NetworkRepository
import ru.ttmf.mark.preference.PreferenceController

class SettingsFragment : BaseFragment() {

    override fun getLayoutId() = R.layout.fragment_settings

    private var url = PreferenceController.getInstance().url
    private var mark_id = PreferenceController.getInstance().markId
    private var protocol = PreferenceController.getInstance().secureProtocol
    private var isRememberAuth = PreferenceController.getInstance().isRememberAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        et_service_url.setText(url)
        et_mark_id.setText(mark_id)
        cb_remember_auth.isChecked = isRememberAuth

        when (protocol) {
            TLS_1_0.javaName() -> rb_tls_1_0.isChecked = true
            TLS_1_1.javaName() -> rb_tls_1_1.isChecked = true
            TLS_1_2.javaName() -> rb_tls_1_2.isChecked = true
            else -> rb_ssl_3.isChecked = true
        }

        rg_protocols.setOnCheckedChangeListener { _, id ->
            protocol = when (id) {
                R.id.rb_tls_1_0 -> TLS_1_0.javaName()
                R.id.rb_tls_1_1 -> TLS_1_1.javaName()
                R.id.rb_tls_1_2 -> TLS_1_2.javaName()
                else -> SSL_3_0.javaName()
            }
        }

        btn_save.setOnClickListener {

            var url = et_service_url.text.toString()
            var temp_mark_id = et_mark_id.text.toString()

            if (url.isNotEmpty()) {
                if (temp_mark_id.isNotEmpty()) {

                    if (!URLUtil.isValidUrl(url) || url[url.length - 1] != '/') {
                        til_service_url.error = getString(R.string.url_setting_error)
                        return@setOnClickListener
                    }

                    if (!(temp_mark_id.length.equals(14) && isNumeric(temp_mark_id))) {
                        til_mark_id.error = getString(R.string.mark_id_error)
                        return@setOnClickListener
                    }

                    val isRememberAuth = cb_remember_auth.isChecked

                    PreferenceController.getInstance().apply {
                        this.url = url
                        this.secureProtocol = protocol
                        this.isRememberAuth = isRememberAuth
                        this.markId = temp_mark_id

                        if (!isRememberAuth) {
                            login = ""
                            password = ""
                        }
                    }

                    hideKeyboard()
                    NetworkRepository.refresh()
                    activity?.onBackPressed()

                } else
                    til_mark_id.error = getString(R.string.mark_id_is_empty)
            } else
                til_service_url.error = getString(R.string.field_is_empty)
        }
    }

    fun isNumeric(strNum: String): Boolean {
        try {
            val d = java.lang.Long.parseLong(strNum)
        } catch (nfe: NumberFormatException) {
            return false
        } catch (nfe: NullPointerException) {
            return false
        }

        return true
    }
}