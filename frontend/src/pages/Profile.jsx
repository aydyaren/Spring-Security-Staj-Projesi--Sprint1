import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import { getCurrentUser } from "../services/authService";

function Profile() {

    // Giriş yapan kullanıcının bilgilerini tutar.
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

    useEffect(() => {

        const loadData = async () => {

            await loadUser();

        };

        loadData().catch(console.error);

    }, []);

    return (

        <div className="container mt-5">

            <h2>Profile</h2>

            {

                user && (

                    <div className="card mt-4" style={{ maxWidth: "500px" }}>

                        <div className="card-body">

                            <p>
                                <strong>First Name:</strong> {user.firstName}
                            </p>

                            <p>
                                <strong>Last Name:</strong> {user.lastName}
                            </p>

                            <p>
                                <strong>Username:</strong> {user.username}
                            </p>

                            <p>
                                <strong>E-mail:</strong> {user.email}
                            </p>

                            <p>
                                <strong>Role:</strong> {user.role}
                            </p>

                        </div>

                    </div>

                )

            }

            <button
                className="btn btn-secondary mt-3"
                onClick={() => navigate("/dashboard")}
            >
                Return
            </button>

        </div>

    );

}

export default Profile;