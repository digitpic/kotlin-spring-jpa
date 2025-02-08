package com.jpa.study.member.service

import com.jpa.study.domain.member.model.Member
import com.jpa.study.domain.member.service.MemberService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // 각 테스트 이후 Spring 컨텍스트 다시 로드 (초기 DB 상태 유지를 위함)
class MemberServiceTest {
    @Autowired
    private lateinit var memberService: MemberService

    @Nested
    inner class Transactional_어노테이션_포함_여부 {
        @Test
        fun includeTransactionalAnnotation() {
            // when
            val returned: Member = memberService.includeTransactionalAnnotation()

            // then
            assertThat(returned.age).isNotEqualTo(30)
        }

        @Test
        fun excludeTransactionalAnnotation() {
            // when
            val returned: Member = memberService.excludeTransactionalAnnotation()

            // then
            assertThat(returned.age).isNotEqualTo(30)
        }
    }

    @Nested
    inner class JPA_JPQL_NativeQuery {
        @Test
        fun jpa_를_사용하는_경우() {
            // when
            val returned: Member = memberService.jpa()

            // then
            assertThat(returned.age).isEqualTo(30)
        }

        @Test
        fun jpql_을_사용하는_경우() {
            // when
            val returned: Member = memberService.jpql()

            // then
            assertThat(returned.age).isEqualTo(30)
        }

        @Test
        fun nativeQuery_를_사용하는_경우() {
            // when
            val returned: Member = memberService.nativeQuery()

            // then
            assertThat(returned.age).isEqualTo(30)
        }
    }

    @Nested
    inner class 특정_상황 {
        @Test
        fun flushInTheMiddle() {
            // when
            val returned: Member = memberService.flushInTheMiddle()

            // then
            assertThat(returned.age).isNotEqualTo(30)
        }

        @Test
        fun complex() {
            // when
            val returned: Member = memberService.complex()

            // then
            assertThat(returned.age).isNotEqualTo(30)
        }
    }
}
