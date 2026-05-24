package com.techub.api.repository;

import com.techub.api.domain.Followers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FollowersRepository extends SoftDeleteRepository<Followers, Long> {
    List<Followers> findByFollowingId(Long followingId);
    List<Followers> findByFollowerId(Long followerId);
    Page<Followers> findByFollowingId(Long followingId, Pageable pageable);
    Page<Followers> findByFollowerId(Long followerId, Pageable pageable);
    java.util.Optional<Followers> findByFollowerIdAndFollowingId(Long followerId, Long followingId);
}