package com.jpa.study.domain.member.service

import com.jpa.study.domain.member.model.Member
import com.jpa.study.domain.member.repository.MemberRepository
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class MemberService(
    private val memberRepository: MemberRepository
) {
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    /* === [예제 1번] === */
    @Transactional // 영속성 컨텍스트 열림
    fun includeTransactionalAnnotation(): Member {
        val member = memberRepository.findById(1L).get() // select

        member.age = 35 // 값 변경 (update 쿼리를 쓰기 지연 저장소에 쌓지는 않음)

        memberRepository.save(member)
        // save 는 isNew() 일 때만 persist() 가 실행되므로 merge() 가 실행 됨
        // find() 로 entity 를 조회 했기에 이미 영속 상태
        // merge() 가 실행 된다고 쓰기 지연 저장소에 쿼리가 쌓이거나, 영속 상태가 달라지거나 하지 않음
        // 여기서는 save 를 통해 변화되는 부분 없음
        // 즉 merge() 가 실행 된다고 해서 쓰기 지연 저장소에 update 쿼리가 쌓이는 게 아님

        println("result: ${member.age}") // result: 35

        return member
    }
    // 메서드가 종료 되면서 transaction 이 commit 됨
    // transaction 이 commit 되면 변경 감지가 일어남
    // update 쿼리를 쓰기 지연 저장소에 쌓음
    // 그 이후에 쓰기 지연 저장소에 쌓인 쿼리들(update)을 DB 에 전송

    /* === [예제 2번] === */
    // 영속성 컨텍스트 열리지 않음
    fun excludeTransactionalAnnotation(): Member {
        val member = memberRepository.findById(1L).get() // select
        // 현재 메서드에서는 영속성 컨텍스트가 열리지 않은 상태
        // findById() 메서드에는 @Transactional 어노테이션이 없지만,
        // SimpleJpaRepository 클래스는 @Transactional 어노테이션이 붙어 있어서
        // @Transactional 이 findById() 메서드에도 전파 됨
        // 그렇기에 영속성 컨텍스트가 열림
        // 영속성 컨텍스트가 열렸으므로 1차 캐시 사용 가능
        // select 쿼리를 DB 에 날려서 데이터를 가져옴
        // 가져온 데이터를 영속성 컨텍스트에 들고 와 초기 상태를 스냅샷으로 저장
        // 가져온 데이터를 1차 캐시에 저장
        // findById() 메서드가 종료 되면서 transaction 이 commit 됨
        // transaction 이 commit 되면 변경 감지가 일어남
        // 변경 사항이 없으니 쓰기 지연 저장소에 쿼리 쌓지 않음
        // 쓰기 지연 저장소에 쌓인 쿼리 없음, DB 로 쿼리 전송하지 않음

        member.age = 35 // 값 변경 (update 쿼리를 쓰기 지연 저장소에 쌓지는 않음)

        memberRepository.save(member) // select, update 쿼리 DB 로 전송
        // 현재 메서드에서는 영속성 컨텍스트가 열리지 않은 상태
        // save 메서드에서는 @Transactional 어노테이션을 사용하기에 영속성 컨텍스트가 열림
        // (@Transactional 어노테이션이 없더라도
        // save() 메서드가 있는 SimpleJpaRepository 클래스에 @Transactional 어노테이션이 붙어 있어서
        // @Transactional 이 save() 메서드에도 전파 됨
        // 영속성 컨텍스트가 열렸으므로 1차 캐시 사용 가능
        // select 쿼리를 DB 에 날려서 데이터를 가져옴
        // 가져온 데이터를 영속성 컨텍스트에 들고 와 초기 상태를 스냅샷으로 저장
        // 가져온 데이터를 1차 캐시에 저장
        // 1차 캐시에 저장된 member.age 를 35 로 변경
        // 쓰기 지연 저장소에 update 쿼리가 쌓이지는 않음
        // save() 메서드가 종료 되면서 transaction 이 commit 됨
        // transaction 이 commit 되면 변경 감지가 일어남
        // update 쿼리를 쓰기 지연 저장소에 쌓음
        // 그 이후에 쓰기 지연 저장소에 쌓인 쿼리들(update)을 DB 에 전송

        println("result: ${member.age}") // result: 35

        return member
    }
    // @Transactional 어노테이션이 없으니
    // 영속성 컨텍스트가 열리지 않음
    // 메서드 그냥 종료 됨
    // 1차 캐시가 없으니 변경 감지 일어나지 않음
    // 쓰기 지연 저장소도 없음

    /* === [예제 3번] === */
    @Transactional // 영속성 컨텍스트 열림
    fun jpa(): Member {
        val member1 = memberRepository.findById(1L).get() // select
        val member2 = memberRepository.findById(1L).get() // 영속성 컨텍스트에 있는 member 가져옴

        println("result: ${member1 === member2}") // result: true
        // member1 은 select 쿼리를 DB 에 전송해 값을 받아와 1차 캐시에 저장
        // member2 는 1차 캐시에 저장된 프록시 객체를 사용함
        // 그래서 member1 === member2 는 true

        return member1
    }
    // 메서드가 종료 되면서 transaction 이 commit 됨
    // transaction 이 commit 되면 변경 감지가 일어남
    // 변경 사항이 없으니 쓰기 지연 저장소에 쿼리 쌓지 않음
    // 쓰기 지연 저장소에 쌓인 쿼리 없음, DB 로 쿼리 전송하지 않음

    /* === [예제 4번] === */
    @Transactional // 영속성 컨텍스트 열림
    fun jpql(): Member {
        val member1 = memberRepository.findById(1L).get() // select
        val member2 = memberRepository.findByIdWithJPQL(1L) // select
        // JPQL: 영속성 컨텍스트 내 값 존재 유무에 상관 없이 쿼리가 1번은 무조건 실행 됨

        println("result: ${member1 === member2}") // result: true
        // JPQL 이라서 쿼리가 한 번은 나감
        // 그런데 가져오고 영속성 컨텍스트에 존재하는 값과 비교해보니 이미 동일한 값이 존재함
        // 그러면 영속성 컨텍스트에 존재하는 프록시 객체를 사용함 (default setting)
        // 그래서 member1 === member2 는 true

        return member1
    }
    // 메서드가 종료 되면서 transaction 이 commit 됨
    // transaction 이 commit 되면 변경 감지가 일어남
    // 변경 사항이 없으니 쓰기 지연 저장소에 쿼리 쌓지 않음
    // 쓰기 지연 저장소에 쌓인 쿼리 없음, DB 로 쿼리 전송하지 않음

    /* === [예제 5번] === */
    @Transactional // 영속성 컨텍스트 열림
    fun nativeQuery(): Member {
        val member1 = memberRepository.findById(1L).get() // select
        val member2 = memberRepository.findByIdWithNative(1L) // select
        // NativeQuery: JPQL 과 같이, 영속성 컨텍스트 내 값 존재 유무에 상관 없이 쿼리가 1번은 무조건 실행 됨

        println("result: ${member1 === member2}") // result: true
        // NativeQuery 라서 쿼리가 한 번은 나감
        // 그런데 가져오고 영속성 컨텍스트에 존재하는 값과 비교해보니 이미 동일한 값이 존재함
        // 그러면 영속성 컨텍스트에 존재하는 프록시 객체를 사용함 (default setting)
        // 그래서 member1 === member2 는 true

        return member1
    }
    // 메서드가 종료 되면서 transaction 이 commit 됨
    // transaction 이 commit 되면 변경 감지가 일어남
    // 변경 사항이 없으니 쓰기 지연 저장소에 쿼리 쌓지 않음
    // 쓰기 지연 저장소에 쌓인 쿼리 없음, DB 로 쿼리 전송하지 않음

    /*
        JPQL, NativeQuery 는 영속성 컨텍스트(1차 캐시) 를 조회하는 게 아니라 DB를 조회하는 쿼리

        그렇기에 JPQL, NativeQuery 는 쿼리를 무조건 실행 하고,
        가져온 값을 영속성 컨텍스트에 저장함

        JPQL, NativeQuery 는 실행 되기 전에 flush() 가 자동으로 실행 됨

        만약 flush() 를 실행하지 않으면
        쓰기 지연 저장소에 쌓인 쿼리들은 아직 DB 에 반영되지 않은 상태이기 때문에
        이 변경 사항들이 JPQL, NativeQuery 결과에 반영 되지 않게 됨
     */

    /* === [예제 6번] === */
    @Transactional // 영속성 컨텍스트 열림
    fun flushInTheMiddle(): Member {
        val member = memberRepository.findById(1L).get() // select

        member.age = 35 // 값 변경 (update 쿼리를 쓰기 지연 저장소에 쌓지는 않음)

        entityManager.flush() // flush 가 실행 되기 전에 변경 감지 수행 (쓰기 지연 저장소에 update 쿼리 쌓음)
        // 쓰기 지연 저장소에 쌓인 쿼리들(update) DB 로 전송

        println("result: ${member.age}") // result: 35

        memberRepository.save(member)
        // save 는 isNew() 일 때만 persist() 가 실행되므로 merge() 가 실행 됨
        // find() 로 entity 를 조회 했기에 이미 영속 상태
        // merge() 가 실행 된다고 쓰기 지연 저장소에 쿼리가 쌓이거나, 영속 상태가 달라지거나 하지 않음
        // 여기서는 save 를 통해 변화되는 부분 없음
        // 즉 merge() 가 실행 된다고 해서 쓰기 지연 저장소에 update 쿼리가 쌓이는 게 아님

        return member
    }
    // 메서드가 종료 되면서 transaction 이 commit 됨
    // transaction 이 commit 되면 변경 감지가 일어남
    // 변경 사항이 없으니 쓰기 지연 저장소에 쿼리 쌓지 않음
    // 쓰기 지연 저장소에 쌓인 쿼리 없음, DB 로 쿼리 전송하지 않음

    /* === [예제 7번] === */
    @Transactional // 영속성 컨텍스트 열림
    fun complex(): Member {
        val alice = memberRepository.findById(1L).get() // select
        val bob = memberRepository.findById(2L).get() // select

        alice.age = 40 // 값 변경 (update 쿼리를 쓰기 지연 저장소에 쌓지는 않음)
        memberRepository.delete(bob) // 쓰기 지연 저장소에 delete 쿼리 쌓음

        val newAlice = memberRepository.findByIdWithJPQL(1L) // select
        // JPQL 이 실행 되기 전에 자동으로 flush 실행
        // flush 가 실행 되기 전에 변경 감지 수행 (쓰기 지연 저장소에 update 쿼리가 쌓임)
        // flush 를 통해 쓰기 지연 저장소에 쌓인 delete, update 쿼리를 DB 에 전송
        // 하이버네이트는 insert > update > delete 의 우선순위를 가지고, 쌓인 쿼리들 중에서 높은 순위부터 전송함
        // 그래서 update, delete 순서로 쿼리가 전송 됨
        // flush 후, select 쿼리 DB 에 전송
        // 그래서 쓰기 지연 저장소에 있던 update, delete 먼저 전송 되고, select 쿼리 전송 됨

        val newBob = Member(id = 2L, name = "Bob", age = 99)

        entityManager.persist(newBob) // 쓰기 지연 저장소에 insert 쿼리 쌓음
        entityManager.flush() // flush 가 실행 되기 전에 변경 감지 수행 (쓰기 지연 저장소에 쿼리 쌓지 않음)
        // 쓰기 지연 저장소에 쌓인 쿼리들(insert)을 DB 에 전송

        val nativeBob = memberRepository.findByIdWithNative(2L) // select

        memberRepository.save(newAlice)
        // save 는 isNew() 일 때만 persist() 가 실행되므로 merge() 가 실행 됨
        // find() 로 entity 를 조회 했기에 이미 영속 상태
        // merge() 가 실행 된다고 쓰기 지연 저장소에 쿼리가 쌓이거나, 영속 상태가 달라지거나 하지 않음
        // 여기서는 save 를 통해 변화되는 부분 없음
        // 즉 merge() 가 실행 된다고 해서 쓰기 지연 저장소에 update 쿼리가 쌓이는 게 아님

        memberRepository.save(nativeBob)
        // save 는 isNew() 일 때만 persist() 가 실행되므로 merge() 가 실행 됨
        // find() 로 entity 를 조회 했기에 이미 영속 상태
        // merge() 가 실행 된다고 쓰기 지연 저장소에 쿼리가 쌓이거나, 영속 상태가 달라지거나 하지 않음
        // 여기서는 save 를 통해 변화되는 부분 없음
        // 즉 merge() 가 실행 된다고 해서 쓰기 지연 저장소에 update 쿼리가 쌓이는 게 아님

        println("result(Alice): " + alice.age)  // 40
        println("result(NewAlice): " + newAlice.age)  // 40
        println("result(Bob): " + bob.age) // 25
        println("result(NewBob): " + newBob.age) // 99
        println("result(NativeBob): " + nativeBob.age) // 99

        return alice
    }
    // 메서드가 종료 되면서 transaction 이 commit 됨
    // transaction 이 commit 되면 변경 감지가 일어남
    // 변경 사항이 없으니 쓰기 지연 저장소에 쿼리 쌓지 않음
    // 쓰기 지연 저장소에 쌓인 쿼리 없음, DB 로 쿼리 전송하지 않음
}
