package com.ll.ebook.app.member.repository;

import com.ll.ebook.app.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByUsername(String username);

    Member findByEmail(String email);

    Member findByUsernameAndEmail(String username, String email);
}