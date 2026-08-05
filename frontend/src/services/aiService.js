import axiosInstance from "../api/axios";

const AI_BASE_URL = "/api/ai";

const extractErrorMessage = (
    error,
    fallbackMessage
) => {

    const responseData = error?.response?.data;

    if (responseData?.message) {
        return responseData.message;
    }

    if (error?.message) {
        return error.message;
    }

    return fallbackMessage;
};

export const generateTags = async (code) => {

    try {

        const response = await axiosInstance.post(
            `${AI_BASE_URL}/generate-tags`,
            {
                code,
            }
        );

        return response.data;

    } catch (error) {

        throw new Error(
            extractErrorMessage(
                error,
                "Unable to generate tags."
            )
        );

    }

};

export const summarizeCode = async (code) => {

    try {

        const response = await axiosInstance.post(
            `${AI_BASE_URL}/summarize`,
            {
                code,
            }
        );

        return response.data;

    } catch (error) {

        throw new Error(
            extractErrorMessage(
                error,
                "Unable to summarize code."
            )
        );

    }

};

export const explainCode = async (code) => {

    try {

        const response = await axiosInstance.post(
            `${AI_BASE_URL}/explain`,
            {
                code,
            }
        );

        return response.data;

    } catch (error) {

        throw new Error(
            extractErrorMessage(
                error,
                "Unable to explain code."
            )
        );

    }

};