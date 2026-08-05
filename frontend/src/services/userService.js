import axiosInstance from "../api/axios";

// ===============================
// PROFILE
// ===============================

// Logged-in profile
export const getProfile = async () => {
  const response = await axiosInstance.get(
    "/api/users/profile"
  );

  return response.data;
};

// Public profile by exact username
export const getPublicProfile = async (username) => {
  const normalizedUsername =
    String(username || "").trim();

  if (!normalizedUsername) {
    throw new Error("Username is required.");
  }

  const response = await axiosInstance.get(
    `/api/users/public/${encodeURIComponent(
      normalizedUsername
    )}`
  );

  return response.data;
};

// User by ID
export const getUserById = async (userId) => {
  const response = await axiosInstance.get(
    `/api/users/${userId}`
  );

  return response.data;
};

// Update profile
export const updateProfile = async (profileData) => {
  const response = await axiosInstance.put(
    "/api/users/profile",
    profileData
  );

  return response.data;
};

// Upload profile image
export const uploadProfileImage = async (imageFile) => {
  const formData = new FormData();

  formData.append("image", imageFile);

  const response = await axiosInstance.put(
    "/api/users/profile/image",
    formData,
    {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    }
  );

  return response.data;
};

// Delete profile
export const deleteProfile = async () => {
  const response = await axiosInstance.delete(
    "/api/users/profile"
  );

  return response.data;
};

// ===============================
// FOLLOW
// ===============================

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

// Requires backend endpoint:
// GET /api/users/following/{userId}
export const isFollowing = async (userId) => {
  const response = await axiosInstance.get(
    `/api/users/following/${userId}`
  );

  return response.data;
};