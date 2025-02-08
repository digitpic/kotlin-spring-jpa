package com.jpa.study.domain.member.repository

import com.jpa.study.domain.member.model.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface MemberRepository : JpaRepository<Member, Long> {
    @Query("SELECT m FROM Member m WHERE m.id = :id", nativeQuery = false)
    fun findByIdWithJPQL(id: Long): Member

    @Query("SELECT * FROM MEMBERS WHERE id = :id", nativeQuery = true)
    fun findByIdWithNative(id: Long): Member
}
