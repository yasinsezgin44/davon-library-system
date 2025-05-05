import {
  Create,
  SimpleForm,
  TextInput,
  PasswordInput,
  SelectInput,
} from "react-admin";

export const UserCreate = () => (
  <Create>
    <SimpleForm>
      {/* Define fields based on our AdminUser interface */}
      <TextInput source="name" validate={required()} />
      <TextInput source="email" type="email" validate={required()} />
      {/* Use PasswordInput for passwords */}
      <PasswordInput source="password" validate={required()} />
      {/* Allow setting the role - adjust choices as needed */}
      <SelectInput
        source="role"
        choices={[
          { id: "Admin", name: "Admin" },
          { id: "Member", name: "Member" },
        ]}
        validate={required()}
        defaultValue="Member"
      />
      {/* createdAt will likely be set automatically by backend/dataProvider */}
      {/* id is usually generated automatically */}
    </SimpleForm>
  </Create>
);

// Basic required field validation
const required = () => (value: any) => value ? undefined : "Required";
