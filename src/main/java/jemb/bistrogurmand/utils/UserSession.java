package jemb.bistrogurmand.utils;

import jemb.bistrogurmand.application.App;

public class UserSession {
        private static User currentUser;

        public static void setCurrentUser(User user) {
            currentUser = user;
        }

        public static User getCurrentUser() {
            return currentUser;
        }

        // Opcional: Limpiar sesión al hacer logout
        public static void clearCurrentUser() {
            currentUser = null;
            App.loadView("login");
        }
}
