import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import { getCurrentUser } from "../services/authService";

import Sidebar from "../components/Sidebar";

import "../styles/Profile.css";

function Profile() {

    // Giriş yapan kullanıcının bilgilerini tutar.
    const [user, setUser] = useState(null);

    // Sayfa yönlendirmelerini sağlar.
    const navigate = useNavigate();

    // Backend'den giriş yapan kullanıcı bilgilerini getirir.
    const loadUser = async () => {

        try {

            const response = await getCurrentUser();

            setUser(response);

        } catch (error) {

            console.error(error);

        }

    };

    useEffect(() => {

        const loadData = async () => {

            await loadUser();

        };

        loadData().catch(console.error);

    }, []);

    return (

        // Profile sayfasının tamamı
        <div className="documents">

            {/* Sol Menü */}
            <Sidebar />

            {/* Sağ taraftaki içerik */}
            <main className="documents-content">

                {/* Sayfa başlığı */}
                <section className="documents-header">

                    <div>

                        <h1>Profile</h1>

                        <p>

                            View your account information.

                        </p>

                    </div>

                </section>

                {/* Profil Kartı */}
                {

                    user && (

                        <div className="profile-card">

                            {/* Ad */}
                            <div className="profile-row">

                                <span>First Name</span>

                                <strong>{user.firstName}</strong>

                            </div>

                            {/* Soyad */}
                            <div className="profile-row">

                                <span>Last Name</span>

                                <strong>{user.lastName}</strong>

                            </div>

                            {/* Kullanıcı Adı */}
                            <div className="profile-row">

                                <span>Username</span>

                                <strong>{user.username}</strong>

                            </div>

                            {/* Email */}
                            <div className="profile-row">

                                <span>E-mail</span>

                                <strong>{user.email}</strong>

                            </div>

                            {/* Rol */}
                            <div className="profile-row">

                                <span>Role</span>

                                <strong>{user.role}</strong>

                            </div>

                        </div>

                    )

                }

                {/* Dashboard'a Dön */}
                <button
                    className="upload-button"
                    onClick={() => navigate("/dashboard")}
                >

                    Return Dashboard

                </button>

            </main>

        </div>

    );

}

export default Profile;