package com.techub.api.service;

import com.techub.api.domain.Course;
import com.techub.api.domain.Followers;
import com.techub.api.dto.FollowesGetResponseDTO;
import com.techub.api.domain.Student;
import com.techub.api.repository.FollowersRepository;
import com.techub.api.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    // Cursos que o estudante segue
    public List<Long> getFollowingCourses(Long studentId) {

        List<Followers> follows =
                followersRepository.findByFollowerId(studentId);

        return follows.stream()
                .filter(follow -> follow.getFollowing().getCourse() != null)
                .map(follow -> follow.getFollowing().getCourse().getId())
                .distinct()
                .toList();
    }

    public List<FollowesGetResponseDTO> getFollowingDetails(Long studentId) {
        List<Followers> follows =
                followersRepository.findByFollowerId(studentId);

        return follows.stream()
                .map(follow -> {
                    var s = follow.getFollowing();
                    return new FollowesGetResponseDTO(
                            s.getId(),
                            follow.getFollower().getId(),
                            s.getNome(),
                            s.getSemestre(),
                            s.getCourse()
                    );
                })
                .toList();
    }

    public List<FollowesGetResponseDTO> getFollowersDetails(Long studentId) {
        List<Followers> follows =
                followersRepository.findByFollowingId(studentId);

        return follows.stream()
                .map(follow -> {
                    var s = follow.getFollower();
                    return new FollowesGetResponseDTO(
                            s.getId(),
                            follow.getFollower().getId(),
                            s.getNome(),
                            s.getSemestre(),
                            s.getCourse()
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