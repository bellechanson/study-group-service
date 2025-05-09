package com.example.studygroupservice.repository;

import com.example.studygroupservice.entity.StudyMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyMemberRepository extends JpaRepository<StudyMember, Long> {
    Page<StudyMember> findByStudyId(Long studyId, Pageable pageable);
}
