package com.example.studygroupservice.service;

import com.example.studygroupservice.entity.StudyGroup;
import com.example.studygroupservice.entity.StudyMember;
import com.example.studygroupservice.repository.StudyGroupRepository;
import com.example.studygroupservice.repository.StudyMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudyMemberService {

    private final StudyMemberRepository studyMemberRepository;
    private final StudyGroupRepository studyGroupRepository;

    // ✅ 신청 기능 (Create)
    public StudyMember apply(Long studyId, Long userId) {
        StudyGroup study = studyGroupRepository.findById(studyId)
                .orElseThrow(() -> new IllegalArgumentException("스터디 그룹이 없습니다: " + studyId));

        StudyMember member = StudyMember.builder()
                .study(study)
                .userId(userId)
                .status("대기") // 신청 시 기본 상태
                .build();

        return studyMemberRepository.save(member);
    }

    // ✅ 스터디별 신청자 목록 페이징 조회
    public Page<StudyMember> getByStudyPaged(Long studyId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("appliedAt").descending());
        return studyMemberRepository.findByStudyId(studyId, pageable);
    }

    public StudyMember updateStatus(Long id, String newStatus) {
        StudyMember member = studyMemberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("신청자 정보를 찾을 수 없습니다. ID: " + id));

        member.setStatus(newStatus); // 예: "수락"
        return studyMemberRepository.save(member);
    }

}
