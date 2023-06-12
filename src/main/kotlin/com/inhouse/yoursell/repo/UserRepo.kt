package com.inhouse.yoursell.repo

import com.inhouse.yoursell.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepo: JpaRepository<User, Long> {
    fun findByUsername(username: String): User
    fun existsByUsername(username: String): Boolean
}