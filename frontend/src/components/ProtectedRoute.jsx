import { Navigate } from "react-router-dom";

function ProtectedRoute({ children }) {

    // Tarayıcıda kayıtlı Access Token'ı alır.
    const accessToken = localStorage.getItem("accessToken");

    // Access Token yoksa Login sayfasına yönlendirir.
    if (!accessToken) {

        return <Navigate to="/" replace />;

    }

    // Access Token varsa ilgili sayfayı gösterir.
    return children;

}

export default ProtectedRoute;