class User:
    def __init__(self, user_id, name, email):
        self.user_id = user_id
        self.name = name
        self.email = email

    def __str__(self):
        return f"User ID: {self.user_id}, Name: {self.name}, Email: {self.email}"

    def __repr__(self):
         return f"User(user_id='{self.user_id}', name='{self.name}', email='{self.email}')"

