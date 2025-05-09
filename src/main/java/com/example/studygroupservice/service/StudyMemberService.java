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

    /**
     * ✅ 신청 기능 (Create)
     * - 기본 상태: "대기"
     */
    public StudyMember apply(Long studyId, Long userId) {
        StudyGroup study = studyGroupRepository.findById(studyId)
                .orElseThrow(() -> new IllegalArgumentException("스터디 그룹이 없습니다: " + studyId));

        StudyMember member = StudyMember.builder()
                .study(study)
                .userId(userId)
                .status("대기")
                .build();

        return studyMemberRepository.save(member);
    }

    /**
     * ✅ 신청자 목록 페이징 조회
     * - "거절" 상태인 멤버는 제외
     */
    public Page<StudyMember> getByStudyPaged(Long studyId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("appliedAt").descending());
        return studyMemberRepository.findByStudyIdAndStatusNot(studyId, "거절", pageable);
    }

    /**
     * ✅ 상태 변경 (예: 수락 처리)
     */
    public StudyMember updateStatus(Long id, String newStatus) {
        StudyMember member = studyMemberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("신청자 정보를 찾을 수 없습니다. ID: " + id));

        // 이미 수락된 경우 다시 수락 불가
        if ("수락".equals(member.getStatus())) {
            throw new IllegalArgumentException("이미 수락된 신청자입니다.");
        }

        // 수락 처리일 때만 currentMember 증가 및 마감 검사
        if ("수락".equals(newStatus)) {
            StudyGroup group = member.getStudy();

            // 현재 인원이 이미 다 찼으면 수락 불가
            if (group.getCurrentMember() >= group.getMaxMember()) {
                throw new IllegalArgumentException("모집 인원이 이미 가득 찼습니다.");
            }

            // currentMember +1
            group.setCurrentMember(group.getCurrentMember() + 1);

            // 인원이 다 찼다면 자동 마감
            if (group.getCurrentMember().equals(group.getMaxMember())) {
                group.setStatus("마감");
            }

            // 그룹 저장
            studyGroupRepository.save(group);
        }

        // 상태 변경 및 저장
        member.setStatus(newStatus);
        return studyMemberRepository.save(member);
    }


    /**
     * ✅ 마스터가 특정 멤버 강제 탈퇴
     */
    public void kickMember(Long memberId, Long requesterId) {
        StudyMember member = studyMemberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 멤버가 존재하지 않습니다."));

        StudyGroup group = member.getStudy();

        if (!group.getOwnerId().equals(requesterId)) {
            throw new IllegalArgumentException("스터디 마스터만 탈퇴시킬 수 있습니다.");
        }

        // 멤버 제거 (삭제 방식)
        studyMemberRepository.deleteById(memberId);
    }

    /**
     * ✅ 마스터가 신청자 거절 처리
     * - "대기" 상태인 경우만 가능
     */
    public void reject(Long memberId, Long requesterId) {
        StudyMember member = studyMemberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 신청자를 찾을 수 없습니다."));

        StudyGroup group = member.getStudy();

        // 마스터 검증
        if (!group.getOwnerId().equals(requesterId)) {
            throw new IllegalArgumentException("스터디 마스터만 거절할 수 있습니다.");
        }

        // 상태 검증
        if (!"대기".equals(member.getStatus())) {
            throw new IllegalArgumentException("대기 상태인 신청자만 거절할 수 있습니다.");
        }

        // ✅ 거절 = DB에서 삭제 처리
        studyMemberRepository.deleteById(memberId);
    }

}
