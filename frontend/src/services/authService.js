import axiosInstance from "./axios";

//Register new user
export const registerUser = async (userData) => {
    const response = await axiosInstance.post(
        "/api/auth/register",
        userData
    );

    return response.data;
};

//login new user
export const loginUser = async (loginData) => {
    const response = await axiosInstance.post(
        "/api/auth/login",
        loginData
    );

    return response.data;
};

export const forgotPassword = async (email) => {

    const response = await axiosInstance.post(
        "/api/auth/forgot-password",
        {
            email,
        }
    );

    return response.data;

};

export const verifyOtp = async (email, otp) => {

    const response = await axiosInstance.post(
        "/api/auth/verify-otp",
        {
            email,
            otp,
        }
    );

    return response.data;

};


export const resetPassword = async (data) => {

    const response = await axiosInstance.post(
        "/api/auth/reset-password",
        data
    );

    return response.data;
};

export const changePassword = async (data) => {

    const response = await axiosInstance.post(
        "/api/auth/change-password",
        data
    );

    return response.data;
};

export const logout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user"); // if you store user info
};