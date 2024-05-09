package com.example.play2plat_tpcm

class IsAdmin(private var adminStatus: Boolean) {

    fun isAdmin(): Boolean {
        return adminStatus
    }

    fun setAdmin(admin: Boolean) {
        adminStatus = admin
    }
}
