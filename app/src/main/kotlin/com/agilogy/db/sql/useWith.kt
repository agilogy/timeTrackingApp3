package com.agilogy.db.sql

inline fun <T : AutoCloseable?, R> T.useWith(block: T.() -> R): R =
    this.use { with(it) { block() } }