package com.techub.api.service;

import com.techub.api.domain.Followers;
import com.techub.api.dto.FollowesGetResponseDTO;
import com.techub.api.domain.Student;
import com.techub.api.repository.FollowersRepository;
import com.techub.api.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

        List<Followers> follows =
                followersRepository.findByFollowerId(studentId);

        return follows.stream()
                .map(follow -> follow.getFollowing().getId())
                .toList();
        }

        public List<FollowesGetResponseDTO> getFollowingDetails(Long studentId, int limit) {
                int pageSize = Math.max(1, limit);

                List<Followers> follows =
                                followersRepository.findByFollowerId(studentId, PageRequest.of(0, pageSize)).getContent();

        return follows.stream()
                .map(follow -> {
                    var s = follow.getFollowing();
                    return new FollowesGetResponseDTO(
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
        }

        public List<FollowesGetResponseDTO> getFollowersDetails(Long studentId, int limit) {
                int pageSize = Math.max(1, limit);

                List<Followers> follows =
                                followersRepository.findByFollowingId(studentId, PageRequest.of(0, pageSize)).getContent();

        return follows.stream()
                .map(follow -> {
                    var s = follow.getFollower();
                    return new FollowesGetResponseDTO(
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
        var existing = followersRepository.findByFollowerIdAndFollowingId(followerId, followingId);
        if (existing.isPresent()) {
            followersRepository.delete(existing.get());
        }
    }
}