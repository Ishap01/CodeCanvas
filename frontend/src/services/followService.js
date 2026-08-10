import axiosInstance from "../api/axios";

export const followUser = async (userId) => {
    const response = await axiosInstance.post(
        `/api/users/follow/${userId}`
    );

    return response.data;
};

export const unfollowUser = async (userId) => {
    const response = await axiosInstance.delete(
        `/api/users/unfollow/${userId}`
    );

    return response.data;
};

export const getFollowersCount = async (userId) => {
    const response = await axiosInstance.get(
        `/api/users/${userId}/followers/count`
    );

    return response.data;
};

export const getFollowingCount = async (userId) => {
    const response = await axiosInstance.get(
        `/api/users/${userId}/following/count`
    );

    return response.data;
};