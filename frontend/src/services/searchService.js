// import axiosInstance from "../api/axios";

// /*
//  * ============================================
//  * SEARCH SNIPPETS
//  * ============================================
//  */
// export const searchSnippets = async (request) => {
//     const response = await axiosInstance.post(
//         "/api/search/snippets",
//         request
//     );

//     return response.data;
// };

// /*
//  * ============================================
//  * SEARCH HISTORY
//  * ============================================
//  */
// export const getSearchHistory = async () => {
//     const response = await axiosInstance.get(
//         "/api/search/history"
//     );

//     return response.data;
// };

// /*
//  * ============================================
//  * POPULAR SEARCHES
//  * ============================================
//  */
// export const getPopularSearches = async () => {
//     const response = await axiosInstance.get(
//         "/api/search/popular"
//     );

//     return response.data;
// };

// /*
//  * ============================================
//  * AUTOCOMPLETE
//  * ============================================
//  */
// export const getSuggestions = async (keyword) => {
//     const response = await axiosInstance.get(
//         "/api/search/suggestions",
//         {
//             params: { keyword },
//         }
//     );

//     return response.data;
// };

// /*
//  * ============================================
//  * USER SEARCH
//  * ============================================
//  */
// export const searchUsers = async (keyword) => {
//     const response = await axiosInstance.get(
//         "/api/search/users",
//         {
//             params: { keyword },
//         }
//     );

//     return response.data;
// };




import axiosInstance from "../api/axios";

/*
 * ============================================
 * SEARCH SNIPPETS
 * ============================================
 */
export const searchSnippets = async (request) => {
  const response = await axiosInstance.post(
    "/api/search/snippets",
    request
  );

  return response.data;
};

/*
 * ============================================
 * SEARCH HISTORY
 * ============================================
 */
export const getSearchHistory = async () => {
  const response = await axiosInstance.get(
    "/api/search/history"
  );

  return response.data;
};

/*
 * ============================================
 * POPULAR SEARCHES
 * ============================================
 */
export const getPopularSearches = async () => {
  const response = await axiosInstance.get(
    "/api/search/popular"
  );

  return response.data;
};

/*
 * ============================================
 * SNIPPET AUTOCOMPLETE
 * ============================================
 */
export const getSuggestions = async (keyword) => {
  const response = await axiosInstance.get(
    "/api/search/suggestions",
    {
      params: {
        keyword,
      },
    }
  );

  return response.data;
};

/*
 * ============================================
 * USER AUTOCOMPLETE
 * ============================================
 */
export const getUserSuggestions = async (keyword) => {
    const response = await axiosInstance.get(
        "/api/search/users/suggestions",
        {
            params: { keyword },
        }
    );

     return response.data;
 };