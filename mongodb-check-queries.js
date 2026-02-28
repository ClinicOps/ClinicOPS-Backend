// MongoDB commands to check your database state
// Run these in MongoDB client (mongo shell or Atlas UI)

// 1. Check Roles Collection
db.roles.find().pretty()

// 2. Count roles
db.roles.count()

// 3. Check OWNER role specifically
db.roles.findOne({ name: "OWNER" })

// 4. Check permissions count
db.permissions.count()

// 5. List all permission keys
db.permissions.find({}, { _id: 1, domain: 1, resource: 1, action: 1 }).pretty()

// 6. Check if OWNER role has permissions
db.roles.aggregate([
  { $match: { name: "OWNER" } },
  { $unwind: "$permissionIds" },
  { $lookup: { 
      from: "permissions", 
      localField: "permissionIds", 
      foreignField: "_id", 
      as: "permissions" 
    }
  },
  { $project: { "name": 1, "permissions.domain": 1, "permissions.resource": 1, "permissions.action": 1 } }
]).pretty()

// 7. Check Organizations
db.organizations.find().pretty()

// 8. Check Clinics
db.clinics.find().pretty()

// 9. Check Users
db.users.find({ deleted: false }).pretty()

// 10. Check UserRoleAssignments
db.userRoleAssignments.find().pretty()

// 11. Clean up (CAREFUL - only if needed to reset for full re-seeding)
// db.roles.deleteMany({})
// db.permissions.deleteMany({})
// db.organizations.deleteMany({})
// db.clinics.deleteMany({})
// db.users.deleteMany({})
// db.userRoleAssignments.deleteMany({})
