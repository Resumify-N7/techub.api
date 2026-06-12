package com.techub.api.service;

import com.techub.api.domain.Followers;
import com.techub.api.dto.FollowGetResponseDTO;
import com.techub.api.dto.FollowPageDTO;
import com.techub.api.domain.Student;
import com.techub.api.repository.FollowersRepository;
import com.techub.api.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FollowService {

    @Autowired
    private FollowersRepository followersRepository;

    @Autowired
    private StudentRepository studentRepository;

    public List<Long> getFollowingUsers(Long studentId) {
        return followersRepository.findByFollowerId(studentId)
                .stream()
                .map(follow -> follow.getFollowing().getId())
                .toList();
    }

    public FollowPageDTO getFollowingDetails(Long studentId, int page, int size) {
        int safeSize = Math.max(1, size);
        Page<Followers> followPage =
                followersRepository.findByFollowerId(studentId, PageRequest.of(page, safeSize));

        List<FollowGetResponseDTO> data = followPage.getContent().stream()
                .map(follow -> {
                    var s = follow.getFollowing();
                    return new FollowGetResponseDTO(
                            s.getId(),
                            follow.getFollower().getId(),
                            s.getNome(),
                            s.getSemestre(),
                            s.getCourse(),
                            s.getFollowers().size(),
                            s.getAvatar().getUrl()
                    );
                })
                .toList();

        return new FollowPageDTO(data, page, safeSize, followPage.getTotalElements());
    }

    public FollowPageDTO getFollowersDetails(Long studentId, int page, int size) {
        int safeSize = Math.max(1, size);
        Page<Followers> followPage =
                followersRepository.findByFollowingId(studentId, PageRequest.of(page, safeSize));

        List<FollowGetResponseDTO> data = followPage.getContent().stream()
                .map(follow -> {
                    var s = follow.getFollower();
                    return new FollowGetResponseDTO(
                            s.getId(),
                            follow.getFollower().getId(),
                            s.getNome(),
                            s.getSemestre(),
                            s.getCourse(),
                            s.getFollowers().size(),
                            s.getAvatar().getUrl()
                    );
                })
                .toList();

        return new FollowPageDTO(data, page, safeSize, followPage.getTotalElements());
    }

    public void follow(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) {
            throw new RuntimeException("Não é possível seguir a si mesmo");
        }

        Student follower = studentRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("Estudante (follower) não encontrado"));

        Student following = studentRepository.findById(followingId)
                .orElseThrow(() -> new RuntimeException("Estudante (following) não encontrado"));

        var existing = followersRepository.findByFollowerIdAndFollowingId(followerId, followingId);
        if (existing.isPresent()) return;

        Followers f = new Followers();
        f.setFollower(follower);
        f.setFollowing(following);
        followersRepository.save(f);
    }

    public void unfollow(Long followerId, Long followingId) {
        followersRepository.findByFollowerIdAndFollowingId(followerId, followingId)
                .ifPresent(followersRepository::delete);
    }
}