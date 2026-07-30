import axios from "axios";

const api = axios.create({

    baseURL: import.meta.env.VITE_API_URL,

    withCredentials: true

});

// Aynı anda birden fazla 401 gelirse
// modalın sadece bir kez açılması için kullanılır.
let sessionExpiredTriggered = false;

/*
 * Her istekten önce Access Token'ı Authorization header'ına ekler.
 */
api.interceptors.request.use(

    (config) => {

        const token = localStorage.getItem("accessToken");

        if (token) {

            config.headers.Authorization = `Bearer ${token}`;

        }

        return config;

    },

    (error) => Promise.reject(error)

);

/*
 * Gelen cevapları kontrol eder.
 * Login endpoint'i hariç diğer endpointlerde
 * 401 alınırsa oturumun sona erdiğini kabul eder.
 */
api.interceptors.response.use(

    (response) => response,

    (error) => {

        const status = error.response?.status;

        const url = error.config?.url || "";

        // Login ekranındaki yanlış kullanıcı/şifre
        // bu interceptor tarafından yönetilmez.
        if (url.includes("/auth/login")) {

            return Promise.reject(error);

        }

        if (status === 401 && !sessionExpiredTriggered) {

            sessionExpiredTriggered = true;

            localStorage.removeItem("accessToken");

            window.dispatchEvent(

                new CustomEvent("session-expired")

            );

        }

        return Promise.reject(error);

    }

);

/*
 * Modal kapandıktan sonra tekrar
 * yeni bir oturum süresi dolma olayı oluşabilsin.
 */
export const resetSessionExpired = () => {

    sessionExpiredTriggered = false;

};

export default api;