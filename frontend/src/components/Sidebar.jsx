import { useEffect, useState } from "react";
import { NavLink, useNavigate } from "react-router-dom";
import {
    LayoutDashboard,
    FileText,
    Users,
    User,
    LogOut
} from "lucide-react";

import logo from "../assets/logo2.png";

import { getCurrentUser, logout } from "../services/authService";

import "./Sidebar.css";

function Sidebar() {

    // Giriş yapan kullanıcı bilgisi
    const [currentUser, setCurrentUser] = useState(null);

    // Logout modalının açık/kapalı durumu
    const [showLogoutModal, setShowLogoutModal] = useState(false);

    // Sayfa yönlendirmesi
    const navigate = useNavigate();

    /*
     * Kullanıcı bilgilerini yükler.
     */
    useEffect(() => {

        const loadUser = async () => {

            try {

                const response = await getCurrentUser();

                setCurrentUser(response);

            } catch (error) {

                console.error(error);

            }

        };

        loadUser();

    }, []);

    /*
     * Logout modalı açıkken:
     * - Sayfanın scroll'unu kapatır.
     * - ESC tuşu ile modalı kapatır.
     */
    useEffect(() => {

        if (!showLogoutModal) {

            document.body.style.overflow = "auto";

            return;

        }

        document.body.style.overflow = "hidden";

        const handleEsc = (event) => {

            if (event.key === "Escape") {

                setShowLogoutModal(false);

            }

        };

        window.addEventListener("keydown", handleEsc);

        return () => {

            document.body.style.overflow = "auto";

            window.removeEventListener("keydown", handleEsc);

        };

    }, [showLogoutModal]);

    /*
     * Gerçek logout işlemi.
     */
    const handleLogout = async () => {

        try {

            await logout();

        } catch (error) {

            console.error(error);

        }

        localStorage.removeItem("accessToken");

        navigate("/");

    };

    return (

        <>

            <aside className="sidebar">

                {/* Logo */}
                <div className="sidebar-logo">

                    <img
                        src={logo}
                        alt="Argela Logo"
                        className="logo-image"
                    />

                </div>

                {/* Kullanıcı Bilgileri */}
                <div className="sidebar-user">

                    <div className="avatar">

                        {currentUser?.firstName?.charAt(0)}
                        {currentUser?.lastName?.charAt(0)}

                    </div>

                    <div>

                        <h4>

                            {currentUser?.firstName} {currentUser?.lastName}

                        </h4>

                        <p>

                            {currentUser?.role}

                        </p>

                    </div>

                </div>

                {/* Menü */}
                <nav className="sidebar-menu">

                    <NavLink
                        to="/dashboard"
                        className={({ isActive }) =>
                            isActive ? "active" : ""
                        }
                    >

                        <LayoutDashboard size={20} />

                        Dashboard

                    </NavLink>

                    <NavLink
                        to="/documents"
                        className={({ isActive }) =>
                            isActive ? "active" : ""
                        }
                    >

                        <FileText size={20} />

                        Documents

                    </NavLink>

                    {
                        currentUser?.role === "ADMIN" && (

                            <NavLink
                                to="/users"
                                className={({ isActive }) =>
                                    isActive ? "active" : ""
                                }
                            >

                                <Users size={20} />

                                Users

                            </NavLink>

                        )

                    }

                    <NavLink
                        to="/profile"
                        className={({ isActive }) =>
                            isActive ? "active" : ""
                        }
                    >

                        <User size={20} />

                        Profile

                    </NavLink>

                </nav>

                {/* Logout */}
                <button
                    className="logout-btn"
                    onClick={() => setShowLogoutModal(true)}
                >

                    <LogOut size={18} />

                    Logout

                </button>

            </aside>
            {/* ===========================================================
                LOGOUT CONFIRMATION MODAL
            ============================================================ */}

            {

                showLogoutModal && (

                    <div
                        className="logout-modal-overlay"
                        onClick={() => setShowLogoutModal(false)}
                    >

                        <div
                            className="logout-modal"
                            onClick={(event) => event.stopPropagation()}
                        >

                            {/* Başlık */}
                            <div className="logout-modal-header">

                                <h2>

                                    Logout

                                </h2>

                                <p>

                                    Are you sure you want to logout?

                                </p>

                            </div>

                            {/* Butonlar */}
                            <div className="logout-modal-footer">

                                <button
                                    className="logout-cancel-btn"
                                    onClick={() => setShowLogoutModal(false)}
                                >

                                    Cancel

                                </button>

                                <button
                                    className="logout-confirm-btn"
                                    onClick={handleLogout}
                                >

                                    Logout

                                </button>

                            </div>

                        </div>

                    </div>

                )

            }
        </>

    );

}

export default Sidebar;