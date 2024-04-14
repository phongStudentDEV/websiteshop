import { Role } from "../../models/role";

export interface UserResponse {
  id: number;
  phone_number: number;
  fullname: string;
  address: string;
  is_active: boolean;
  date_of_birth: Date;
  facebook_account_id: number;
  google_account_id: number;
  role: Role;
}
