package com.github.crosswall.plugin.option


open class TimeClassOption {

    companion object {
        val FILTER_CLASS_LIST = listOf("R.class", "R$", "Manifest", "BuildConfig")
    }


    var mBeatClass: String? = null

    var test: String? = null
    var test2: String? = null

    private val mTracePackageList = mutableListOf<String>()

    private val mWhiteClassList = mutableListOf<String>()

    private val mWhitePackageList = mutableListOf<String>()


    fun setBeatClass(className: String){
        this.mBeatClass = className
    }

    fun addPackageList(vararg packageName:String){
        packageName.forEach {
            println("==============packageName: $it===============")
        }

        mTracePackageList.addAll(packageName)
    }


    fun isNeedTraceClass(fileName: String): Boolean {
        var isNeed = true
        if (fileName.endsWith(".class")) {
            for (filter in FILTER_CLASS_LIST) {
                if (fileName.contains(filter)) {
                    isNeed = false
                    break
                }
            }
        } else {
            isNeed = false
        }
        return isNeed
    }


    fun isConfigTraceClass(className: String): Boolean {

        fun isInNeedTracePackage(): Boolean {
            var isIn = false
            mTracePackageList.forEach {
                if (className.contains(it)) {
                    isIn = true
                    return@forEach
                }

            }
            return isIn
        }

        fun isInWhitePackage(): Boolean {
            var isIn = false
            mWhiteClassList.forEach {
                if (className.contains(it)) {
                    isIn = true
                    return@forEach
                }

            }
            return isIn
        }

        fun isInWhiteClass(): Boolean {
            var isIn = false
            mWhitePackageList.forEach {
                if (className == it) {
                    isIn = true
                    return@forEach
                }

            }
            return isIn
        }

        return when {
            mTracePackageList.isEmpty() -> !(isInWhitePackage() || isInWhiteClass())
            isInNeedTracePackage() -> !(isInWhitePackage() || isInWhiteClass())
            else -> false
        }

    }


}