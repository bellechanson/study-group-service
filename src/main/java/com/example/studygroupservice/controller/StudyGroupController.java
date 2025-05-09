package com.example.studygroupservice.controller;

import com.example.studygroupservice.entity.StudyGroup;
import com.example.studygroupservice.service.StudyGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/study-groups")
@RequiredArgsConstructor
public class StudyGroupController {

    private final StudyGroupService studyGroupService;

    /**
     * 📌 스터디 모집글 생성 (POST)
     * 요청 본문에 StudyGroup 정보 (ownerId 포함)를 전달받아 저장
     */
    @PostMapping
    public ResponseEntity<StudyGroup> create(@RequestBody StudyGroup group) {
        return ResponseEntity.ok(studyGroupService.create(group));
    }

    /**
     * 📌 스터디 모집글 페이징 조회 (GET)
     * page와 size를 파라미터로 받아 페이지 단위로 최신순 정렬된 목록 반환
     */
    @GetMapping("/paged")
    public ResponseEntity<Page<StudyGroup>> getPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(studyGroupService.getPaged(page, size));
    }

    /**
     * 📌 모집글 단건 조회 (GET)
     * 특정 ID의 스터디 모집글 상세 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<StudyGroup> getById(@PathVariable Long id) {
        return ResponseEntity.ok(studyGroupService.getById(id));
    }

    /**
     * 📌 모집글 수정 (PUT)
     * 작성자(ownerId와 userId가 일치)만 수정 가능
     * @param userId → 요청자 ID, 검증용
     */
    @PutMapping("/{id}")
    public ResponseEntity<StudyGroup> update(
            @PathVariable Long id,
            @RequestBody StudyGroup group,
            @RequestParam Long userId
    ) {
        return ResponseEntity.ok(studyGroupService.update(id, group, userId));
    }

    /**
     * 📌 모집글 삭제 (DELETE)
     * 작성자만 삭제 가능
     * @param userId → 요청자 ID, 검증용
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @RequestParam Long userId
    ) {
        studyGroupService.delete(id, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 📌 모집 마감 처리 (PUT)
     * 상태(status)를 "마감"으로 변경
     */
    @PutMapping("/{id}/close")
    public ResponseEntity<StudyGroup> close(@PathVariable Long id) {
        return ResponseEntity.ok(studyGroupService.close(id));
    }
}
