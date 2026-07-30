import { Navigate, useLocation } from "react-router-dom";

function ProtectedRoute({ children }) {

    const accessToken = localStorage.getItem("accessToken");

    const location = useLocation();

    if (!accessToken) {

        return (

            <Navigate

                to="/"

                replace

                state={{ from: location.pathname }}

            />

        );

    }

    return children;

}

export default ProtectedRoute;