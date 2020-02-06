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
    private var protocol = PreferenceController.getInstance().secureProtocol
    private var isRememberAuth = PreferenceController.getInstance().isRememberAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        et_service_url.setText(url)
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

            if (url.isNotEmpty()) {

                if (!URLUtil.isValidUrl(url) || url[url.length - 1] != '/') {
                    til_service_url.error = getString(R.string.url_setting_error)
                    return@setOnClickListener
                }

                val isRememberAuth = cb_remember_auth.isChecked

                PreferenceController.getInstance().apply {
                    this.url = url
                    this.secureProtocol = protocol
                    this.isRememberAuth = isRememberAuth

                    if (!isRememberAuth) {
                        login = ""
                        password = ""
                    }
                }

                hideKeyboard()
                NetworkRepository.refresh()
                activity?.onBackPressed()

            } else
                til_service_url.error = getString(R.string.field_is_empty)
        }
    }
}