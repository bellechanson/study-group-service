package com.example.studygroupservice.service;

import com.example.studygroupservice.entity.StudyGroup;
import com.example.studygroupservice.entity.StudyMember;
import com.example.studygroupservice.repository.StudyGroupRepository;
import com.example.studygroupservice.repository.StudyMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudyGroupService {

    private final StudyGroupRepository studyGroupRepository;
    private final StudyMemberRepository studyMemberRepository;

    /**
     * 📌 스터디 모집글 생성
     * - 작성자가 포함된 StudyGroup 객체를 받아 저장
     */
//    public StudyGroup create(StudyGroup group) {
//        return studyGroupRepository.save(group);
//    }
//
//    public StudyMember apply(Long studyId, Long userId) {
//        StudyGroup study = new StudyGroup();
//
//        StudyMember member = StudyMember.builder()
//                .study(study)
//                .userId(userId)
//                .status("마스터") // 신청 시 바로 방장
//                .build();
//
//        return studyMemberRepository.save(member);
//    }

    public StudyMember create(StudyGroup group, Long userId) {

        group.setOwnerId(userId);
        StudyGroup study = studyGroupRepository.save(group);
        StudyMember member = StudyMember.builder()
                .study(study)
                .userId(userId)
                .status("마스터") // 신청 시 바로 방장
                .build();

        return studyMemberRepository.save(member);
    }


    /**
     * 📌 모집글 목록 페이징 조회
     * - 최신순(createdAt DESC) 정렬로 페이지별 모집글 반환
     */
    public Page<StudyGroup> getPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return studyGroupRepository.findAll(pageable);
    }

    /**
     * 📌 단일 모집글 조회
     * - ID로 모집글을 조회하고 없으면 예외 발생
     */
    public StudyGroup getById(Long id) {
        return studyGroupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("스터디 그룹이 없습니다. ID: " + id));
    }

    /**
     * 📌 모집글 수정
     * - 요청자(userId)가 작성자(ownerId)와 일치해야 수정 허용
     * - 수정 필드: 제목, 설명, 최대인원, 상태
     */
    public StudyGroup update(Long id, StudyGroup updated, Long userId) {
        StudyGroup group = getById(id);

        if (!group.getOwnerId().equals(userId)) {
            throw new IllegalArgumentException("해당 글을 수정할 권한이 없습니다.");
        }

        group.setTitle(updated.getTitle());
        group.setDescription(updated.getDescription());
        group.setMaxMember(updated.getMaxMember());
        group.setStatus(updated.getStatus());
        return studyGroupRepository.save(group);
    }

    /**
     * 📌 모집글 삭제
     * - 요청자(userId)가 작성자(ownerId)일 경우만 삭제 허용
     */
    public void delete(Long id, Long userId) {
        StudyGroup group = getById(id);
        if (!group.getOwnerId().equals(userId)) {
            throw new IllegalArgumentException("해당 글을 삭제할 권한이 없습니다.");
        }
        studyGroupRepository.deleteById(id);
    }

    /**
     * 📌 모집 마감 처리
     * - 모집 상태를 "마감"으로 설정
     */
    public StudyGroup close(Long id) {
        StudyGroup group = getById(id);
        group.setStatus("마감");
        return studyGroupRepository.save(group);
    }
}
