import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import {
    FileText,
    Users,
    User,
    LogOut
} from "lucide-react";

import LoadingSpinner from "../components/common/LoadingSpinner";

import {
    getCurrentUser,
    logout
} from "../services/authService";

import Sidebar from "../components/Sidebar";

import "../styles/Dashboard.css";

function Dashboard() {

    // Giriş yapan kullanıcı bilgisi.
    const [user, setUser] = useState(null);

    // Sayfanın yüklenme durumu.
    const [loading, setLoading] = useState(true);

    // Sayfa yönlendirmesi.
    const navigate = useNavigate();

    // Kullanıcı bilgilerini getirir.
    const loadUser = async () => {

        try {

            const response = await getCurrentUser();

            setUser(response);

        } catch (error) {

            console.error(error);

        } finally {

            setLoading(false);

        }

    };

    // Users sayfasına gider.
    const goToUsers = () => {

        navigate("/users");

    };

    // Çıkış işlemi.
    const handleLogout = async () => {

        try {

            await logout();

        } catch (error) {

            console.error(error);

        }

        localStorage.removeItem("accessToken");

        navigate("/");

    };

    useEffect(() => {

        loadUser().catch(console.error);

    }, []);

    if (loading) {

        return <LoadingSpinner />;

    }

    return (

        <div className="dashboard">

            {/* Sol Menü */}
            <Sidebar />

            {/* Sağ İçerik */}
            <main className="dashboard-content">

                {/* Hero */}
                <section className="hero">

                    <div>

                        <h1>

                            Welcome back, {user?.firstName} 👋

                        </h1>

                        <p>

                            Manage your documents securely with Argela DMS.

                        </p>

                    </div>

                </section>

                {/* Kartlar */}
                <section className="dashboard-cards">

                    {/* Documents */}
                    <div
                        className="dashboard-card"
                        onClick={() => navigate("/documents")}
                    >

                        <h3 className="card-title">

                            <FileText size={24} />

                            Documents

                        </h3>

                        <p>

                            Upload, update and manage your documents.

                        </p>

                    </div>

                    {/* Users */}
                    {

                        user?.role === "ADMIN" && (

                            <div
                                className="dashboard-card"
                                onClick={goToUsers}
                            >

                                <h3 className="card-title">

                                    <Users size={24} />

                                    Users

                                </h3>

                                <p>

                                    Manage system users and roles.

                                </p>

                            </div>

                        )

                    }

                    {/* Profile */}
                    <div
                        className="dashboard-card"
                        onClick={() => navigate("/profile")}
                    >

                        <h3 className="card-title">

                            <User size={24} />

                            Profile

                        </h3>

                        <p>

                            View and manage your account information.

                        </p>

                    </div>

                    {/* Logout */}
                    <div
                        className="dashboard-card logout-card"
                        onClick={handleLogout}
                    >

                        <h3 className="card-title">

                            <LogOut size={24} />

                            Logout

                        </h3>

                        <p>

                            Sign out safely from your account.

                        </p>

                    </div>

                </section>

            </main>

        </div>

    );

}

export default Dashboard;