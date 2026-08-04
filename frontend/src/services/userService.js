import axiosInstance from "../api/axios";

// Get logged-in user's profile
export const getProfile = async () => {
  const response = await axiosInstance.get("/api/users/profile");
  return response.data;
};

// Get user by ID
export const getUserById = async (userId) => {
  const response = await axiosInstance.get(`/api/users/${userId}`);
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

// Get public profile by username
export const getPublicProfile = async (username) => {
  const response = await axiosInstance.get(
    `/api/users/public/${username}`
  );

  return response.data;
};