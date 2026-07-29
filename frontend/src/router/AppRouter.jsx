import { BrowserRouter, Routes, Route } from "react-router-dom";

import Login from "../pages/Login";
import Dashboard from "../pages/Dashboard";
import Users from "../pages/Users";
import Documents from "../pages/Documents";

import ProtectedRoute from "../components/ProtectedRoute";
import Profile from "../pages/Profile";

function AppRouter() {

    return (

        <BrowserRouter>

            <Routes>

                {/* Giriş sayfasını gösterir. */}
                <Route
                    path="/"
                    element={<Login />}
                />

                {/* Dashboard sayfasını sadece giriş yapan kullanıcılar görebilir. */}
                <Route
                    path="/dashboard"
                    element={
                        <ProtectedRoute>

                            <Dashboard />

                        </ProtectedRoute>
                    }
                />

                {/* Kullanıcı yönetim sayfasını sadece giriş yapan kullanıcılar görebilir. */}
                <Route
                    path="/users"
                    element={
                        <ProtectedRoute>

                            <Users />

                        </ProtectedRoute>
                    }
                />

                {/* Belge yönetim sayfasını sadece giriş yapan kullanıcılar görebilir. */}
                <Route
                    path="/documents"
                    element={
                        <ProtectedRoute>

                            <Documents />

                        </ProtectedRoute>
                    }
                />
                {/* Profil sayfasını sadece giriş yapan kullanıcılar görebilir. */}
                <Route
                    path="/profile"
                    element={
                        <ProtectedRoute>

                            <Profile />

                        </ProtectedRoute>
                    }
                />

            </Routes>

        </BrowserRouter>

    );

}

export default AppRouter;