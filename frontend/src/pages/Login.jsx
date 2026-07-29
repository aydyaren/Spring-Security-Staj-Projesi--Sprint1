import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { login } from "../services/authService";


function Login() {

    // Kullanıcının girdiği username veya email bilgisini tutar.
    const [username, setUsername] = useState("");

    // Kullanıcının girdiği şifreyi tutar.
    const [password, setPassword] = useState("");

    // Sayfalar arasında geçiş yapmak için kullanılır.
    const navigate = useNavigate();

    // Login butonuna basıldığında çalışır.
    const handleLogin = async () => {

        try {

            // Backend'e login isteği gönderir.
            const response = await login(username, password);

            // Backend'den gelen Access Token'ı alır.
            const accessToken = response.accessToken;

            // Access Token'ı tarayıcıda saklar.
            localStorage.setItem("accessToken", accessToken);

            // Login başarılı olunca Dashboard sayfasına yönlendirir.
            navigate("/dashboard");

            // Test amacıyla Access Token'ı ekrana yazdırır.
            console.log(accessToken);

        } catch (error) {

            console.error(error);

        }

    };

    return (

        <div className="container mt-5">

            <h2>Login</h2>

            <div className="mb-3">

                <label className="form-label">
                    Username or Email
                </label>

                <input
                    type="text"
                    className="form-control"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                />

            </div>

            <div className="mb-3">

                <label className="form-label">
                    Password
                </label>

                <input
                    type="password"
                    className="form-control"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                />

            </div>

            <button
                className="btn btn-primary"
                onClick={handleLogin}
            >
                Login
            </button>

        </div>

    );

}

export default Login;