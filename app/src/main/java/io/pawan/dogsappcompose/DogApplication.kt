package io.pawan.dogsappcompose

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import io.branch.referral.Branch
import io.branch.referral.BranchLogger

@HiltAndroidApp
class DogApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        Branch.enableLogging(BranchLogger.BranchLogLevel.VERBOSE)
        Branch.getAutoInstance(this)
    }
}