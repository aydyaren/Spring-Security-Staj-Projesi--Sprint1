import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { login } from "../services/authService";

import "../styles/Login.css";
import logo from "../assets/logo2.png";

import { User, Lock, Eye, EyeOff } from "lucide-react";

function Login() {

    // Kullanıcının girdiği username veya email bilgisini tutar.
    const [username, setUsername] = useState("");

    // Kullanıcının girdiği şifreyi tutar.
    const [password, setPassword] = useState("");

    // Şifreyi göster / gizle
    const [showPassword, setShowPassword] = useState(false);

    // Sayfalar arasında geçiş yapmak için kullanılır.
    const navigate = useNavigate();

    // Login butonuna basıldığında çalışır.
    const handleLogin = async () => {

        console.log("Login button clicked");

        try {

            console.log("Sending request...");

            // Backend'e login isteği gönderir.
            const response = await login(username, password);

            console.log("Response:", response);

            // Backend'den gelen Access Token'ı alır.
            const accessToken = response.accessToken;

            // Token'ı tarayıcıya kaydeder.
            localStorage.setItem("accessToken", accessToken);

            // Dashboard sayfasına yönlendir.
            navigate("/dashboard");

        } catch (error) {
            console.error("LOGIN ERROR:", error);

            if (error.response) {
                console.log("Status:", error.response.status);
                console.log("Data:", error.response.data);
            } else if (error.request) {
                console.log("Request gönderildi ama cevap gelmedi:", error.request);
            } else {
                console.log("İstek oluşturulurken hata:", error.message);
            }
        }

    };

    return (

        <div className="login-page">

            {/* Arka plan efektleri */}
            <div className="background-blur"></div>
            <div className="background-grid"></div>
            <div className="background-wave"></div>

            {/* Login Kartı */}
            <div className="login-card">

                {/* Logo */}
                <img
                    src={logo}
                    alt="Argela Logo"
                    className="login-logo"
                />

                {/* Başlık */}
                <h1>Welcome back</h1>

                {/* Username */}
                <div className="input-group">

                    <User size={20} color="#8d8d98" />

                    <input
                        type="text"
                        placeholder="Username or Email"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                    />

                </div>

                {/* Password */}
                <div className="input-group">

                    <Lock size={20} color="#8d8d98" />

                    <input
                        type={showPassword ? "text" : "password"}
                        placeholder="Password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                    />

                    <span
                        className="eye"
                        onClick={() => setShowPassword(!showPassword)}
                    >
                        {showPassword ? (
                            <EyeOff size={20} color="#8d8d98" />
                        ) : (
                            <Eye size={20} color="#8d8d98" />
                        )}
                    </span>

                </div>

                {/* Login Butonu */}
                <button
                    className="login-button"
                    onClick={handleLogin}
                >
                    LOGIN
                </button>

            </div>

        </div>

    );

}

export default Login;