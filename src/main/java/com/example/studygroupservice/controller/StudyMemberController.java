package com.example.studygroupservice.controller;

import com.example.studygroupservice.entity.StudyMember;
import com.example.studygroupservice.service.StudyMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/study-members")
@RequiredArgsConstructor
public class StudyMemberController {

    private final StudyMemberService studyMemberService;

    // ✅ 1. 스터디 참가 신청 (C)
    @PostMapping
    public ResponseEntity<StudyMember> apply(
            @RequestParam Long studyId,
            @RequestParam Long userId
    ) {
        return ResponseEntity.ok(studyMemberService.apply(studyId, userId));
    }

    // ✅ 2. 특정 스터디의 신청자 페이징 조회 (R)
    @GetMapping("/by-study")
    public ResponseEntity<Page<StudyMember>> getByStudyPaged(
            @RequestParam Long studyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(studyMemberService.getByStudyPaged(studyId, page, size));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudyMember> updateStatus(
            @PathVariable Long id,
            @RequestBody StudyMember updated
    ) {
        return ResponseEntity.ok(studyMemberService.updateStatus(id, updated.getStatus()));
    }

}
