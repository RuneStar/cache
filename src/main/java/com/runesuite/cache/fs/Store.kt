package com.runesuite.cache.fs

import java.nio.file.Path

class Store(val folder: Path) {

    companion object {
        val MAIN_FILE_CACHE_DAT = "main_file_cache.dat2"
        val MAIN_FILE_CACHE_IDX = "main_file_cache.idx"
    }
}