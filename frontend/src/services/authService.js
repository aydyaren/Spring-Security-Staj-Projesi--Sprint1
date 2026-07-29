import api from "./api";

// Kullanıcının login işlemini gerçekleştirir.
export const login = async (login, password) => {

    // Backend'e login isteği gönderir.
    const response = await api.post("/auth/login", {

        login: login,

        password: password

    });

    // Backend'den gelen cevabı döndürür.
    return response.data;



};

// Giriş yapan kullanıcının bilgilerini getirir.
export const getCurrentUser = async () => {

    // Backend'e istek gönderir.
    const response = await api.get("/users/me");

    // Kullanıcı bilgilerini döndürür.
    return response.data;

};

// Kullanıcının çıkış işlemini gerçekleştirir.
export const logout = async () => {

    // Backend'e logout isteği gönderir.
    await api.post("/auth/logout");

};