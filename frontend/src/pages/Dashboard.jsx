import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import {
    getCurrentUser,
    logout
} from "../services/authService";

function Dashboard() {

    // Giriş yapan kullanıcı bilgisini tutar.
    const [user, setUser] = useState(null);

    // Sayfa yönlendirmelerini sağlar.
    const navigate = useNavigate();

    // Backend'den giriş yapan kullanıcıyı alır.
    const loadUser = async () => {

        try {

            const response = await getCurrentUser();

            setUser(response);

        } catch (error) {

            console.error(error);

        }

    };

    // Kullanıcı yönetim sayfasına yönlendirir.
    const goToUsers = () => {

        navigate("/users");

    };

    // Kullanıcının çıkış işlemini gerçekleştirir.
    const handleLogout = async () => {

        try {

            // Backend'deki Refresh Token'ı iptal eder.
            await logout();

        } catch (error) {

            console.error(error);

        }

        // Tarayıcıdaki Access Token'ı siler.
        localStorage.removeItem("accessToken");

        // Login sayfasına yönlendirir.
        navigate("/");

    };

    useEffect(() => {

        const loadData = async () => {

            await loadUser();

        };

        loadData().catch(console.error);

    }, []);

    return (

        <div className="container mt-5">

            <h2>Dashboard</h2>

            {

                user && (

                    <div className="mt-4">

                        {
                            user.role === "ADMIN" && (

                                <button
                                    className="btn btn-primary me-2"
                                    onClick={goToUsers}
                                >
                                    Users
                                </button>

                            )
                        }

                        {
                            (
                                user.role === "ADMIN" ||
                                user.role === "MANAGER" ||
                                user.role === "EMPLOYEE"
                            ) && (

                                <button
                                    className="btn btn-primary me-2"
                                    onClick={() => navigate("/documents")}
                                >
                                    Documents
                                </button>

                            )
                        }

                        <button
                            className="btn btn-danger me-2"
                            onClick={handleLogout}
                        >
                            Logout
                        </button>

                        <button
                            className="btn btn-primary me-2"
                            onClick={() => navigate("/profile")}
                        >
                            Profile
                        </button>

                    </div>

                )

            }

        </div>

    );

}

export default Dashboard;

