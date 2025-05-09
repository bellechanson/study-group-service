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

    // ✅ 마스터가 특정 멤버를 강제 탈퇴
    @DeleteMapping("/{memberId}/kick")
    public ResponseEntity<?> kickMember(
            @PathVariable Long memberId,
            @RequestParam Long requesterId // 마스터 ID (검증용)
    ) {
        studyMemberService.kickMember(memberId, requesterId);
        return ResponseEntity.ok("강제 탈퇴 완료");
    }

    // ✅ 마스터가 신청자를 거절 처리
    @PutMapping("/{id}/reject")
    public ResponseEntity<?> reject(
            @PathVariable Long id,
            @RequestParam Long requesterId
    ) {
        studyMemberService.reject(id, requesterId);
        return ResponseEntity.ok("거절 완료");
    }

    // ✅ 유저가 스터디에서 자발적으로 탈퇴
    @DeleteMapping("/{memberId}/leave")
    public ResponseEntity<?> leaveGroup(
            @PathVariable Long memberId,
            @RequestParam Long userId
    ) {
        studyMemberService.leave(memberId, userId);
        return ResponseEntity.ok("스터디 탈퇴 완료");
    }


}
