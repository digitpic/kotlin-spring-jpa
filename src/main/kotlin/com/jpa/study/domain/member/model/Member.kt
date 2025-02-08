package com.jpa.study.domain.member.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "members")
class Member(
    @Id
    val id: Long = 0L,

    @Column(name = "name")
    var name: String? = null,

    @Column(name = "age")
    var age: Int? = null
)
