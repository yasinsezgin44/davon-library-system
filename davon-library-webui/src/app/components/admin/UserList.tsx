import {
  List,
  Datagrid,
  TextField,
  EmailField,
  DateField,
  EditButton,
  DeleteButton,
  CreateButton,
  TopToolbar,
} from "react-admin";

// Component for custom List Actions
const ListActions = () => (
  <TopToolbar>
    <CreateButton />
    {/* Add other actions like export button if needed */}
  </TopToolbar>
);

export const UserList = () => (
  <List actions={<ListActions />}>
    {" "}
    {/* The main List component wrapper */}
    <Datagrid rowClick="edit">
      {" "}
      {/* Creates the table, rowClick="edit" makes rows clickable to edit */}
      <TextField source="id" /> {/* Displays the 'id' field */}
      <TextField source="name" /> {/* Displays the 'name' field */}
      <EmailField source="email" />{" "}
      {/* Displays the 'email' field, potentially as a mailto link */}
      <TextField source="role" /> {/* Displays the 'role' field */}
      <DateField source="createdAt" label="Created At" showTime />{" "}
      {/* Displays the date, customize formatting as needed */}
      <EditButton /> {/* Adds an explicit Edit button column */}
      <DeleteButton />
    </Datagrid>
  </List>
);
