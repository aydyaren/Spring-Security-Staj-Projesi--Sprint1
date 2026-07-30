import { BrowserRouter, Routes, Route } from "react-router-dom";
import { useEffect, useState } from "react";

import Login from "../pages/Login";
import Dashboard from "../pages/Dashboard";
import Users from "../pages/Users";
import Documents from "../pages/Documents";
import Profile from "../pages/Profile";

import ProtectedRoute from "../components/ProtectedRoute";
import SessionExpiredModal from "../components/SessionExpiredModal";

function AppRouter() {

    /*
     * Session Expired modalının açık/kapalı durumunu tutar.
     */
    const [sessionExpired, setSessionExpired] = useState(false);

    /*
     * Axios tarafından gönderilen
     * "session-expired" eventini dinler.
     */
    useEffect(() => {

        const handleSessionExpired = () => {

            setSessionExpired(true);

        };

        window.addEventListener(
            "session-expired",
            handleSessionExpired
        );

        return () => {

            window.removeEventListener(
                "session-expired",
                handleSessionExpired
            );

        };

    }, []);

    return (

        <BrowserRouter>

            <Routes>

                {/* Login */}

                <Route
                    path="/"
                    element={<Login />}
                />

                {/* Dashboard */}

                <Route
                    path="/dashboard"
                    element={
                        <ProtectedRoute>

                            <Dashboard />

                        </ProtectedRoute>
                    }
                />

                {/* Users */}

                <Route
                    path="/users"
                    element={
                        <ProtectedRoute>

                            <Users />

                        </ProtectedRoute>
                    }
                />

                {/* Documents */}

                <Route
                    path="/documents"
                    element={
                        <ProtectedRoute>

                            <Documents />

                        </ProtectedRoute>
                    }
                />

                {/* Profile */}

                <Route
                    path="/profile"
                    element={
                        <ProtectedRoute>

                            <Profile />

                        </ProtectedRoute>
                    }
                />

            </Routes>

            {/* Session Expired Modal */}

            <SessionExpiredModal

                open={sessionExpired}

                onClose={() => setSessionExpired(false)}

            />

        </BrowserRouter>

    );

}

export default AppRouter;