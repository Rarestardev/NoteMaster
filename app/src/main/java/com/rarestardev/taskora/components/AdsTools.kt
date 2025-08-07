package com.rarestardev.taskora.components

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.adivery.sdk.AdiveryAdListener
import com.adivery.sdk.AdiveryBannerAdView
import com.adivery.sdk.AdiveryNativeAdView
import com.adivery.sdk.BannerSize
import com.rarestardev.taskora.R
import com.rarestardev.taskora.utilities.Constants

@Composable
fun BannerAds() {
    var shouldShowAd by remember { mutableStateOf(false) }

    if (Constants.ADS_VIEW) {
        AndroidView(
            factory = { mContext->
                AdiveryBannerAdView(mContext).apply {
                    setBannerSize(BannerSize.BANNER)
                    setPlacementId(Constants.AD_BANNER_ID)

                    setBannerAdListener(object : AdiveryAdListener() {
                        override fun onAdLoaded() {
                            shouldShowAd = true
                        }

                        override fun onError(error: String?) {
                            shouldShowAd = false
                        }
                    })
                    loadAd()
                }
            },
            modifier = Modifier
                .wrapContentWidth()
                .padding(
                    start = 20.dp,
                    end = 20.dp
                )
                .height(50.dp)
                .let {
                    if (shouldShowAd) it else Modifier.height(0.dp)
                }
                .clip(MaterialTheme.shapes.small)
        )
    }
}

@SuppressLint("InflateParams")
@Composable
fun AdiveryNativeAdLayoutWithTitle(){
    var isShowAd by remember { mutableStateOf(false) }

    if (isShowAd){
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.ad),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(start = 12.dp)
            )
        }
    }

    AndroidView(
        factory = { context ->
           AdiveryNativeAdView(context).apply {
                setNativeAdLayout(R.layout.adivery_native_ad_layout)
                setPlacementId(Constants.AD_NATIVE_ID)
                setListener(object : AdiveryAdListener(){
                    override fun onError(reason: String?) {
                        Log.e(Constants.ADS_LOG,reason ?: "Native ad error load")
                        isShowAd = false
                    }

                    override fun onAdLoaded() {
                        Log.d(Constants.ADS_LOG,"Loaded native ad")
                        isShowAd = true
                    }
                })

                loadAd()
            }


        },
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(colorResource(R.color.second_night_mode))
            .wrapContentHeight()
            .padding(12.dp)
            .let {
                if (isShowAd) it else Modifier.height(0.dp)
            }
    )
}