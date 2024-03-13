package kr.foundcake.role_auth.database

import com.mysql.cj.jdbc.Driver
import kr.foundcake.role_auth.dto.User
import kr.foundcake.role_auth.table.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

object DBManager {

	private val logger = LoggerFactory.getLogger(DBManager::class.java)

	fun init() {
		val name = System.getenv("DB_NAME")
		val pw = System.getenv("DB_PASSWORD")

		Database.connect(
			driver = Driver::class.qualifiedName!!,
			url = "jdbc:mysql://mysql:3306/${name}",
			user = "root",
			password = pw
		)

		logger.info("Connected Database")

		transaction {
			SchemaUtils.create(Users)
		}
		logger.info("initialize table")
	}

	object UserRepo {

		suspend fun find(number: Int): User? {
			var user: User? = null
			newSuspendedTransaction {
				val result: ResultRow? = Users.selectAll().where {
					Users.number eq number
				}.limit(1).singleOrNull()
				if (result !== null) {
					user = User(
						number = result[Users.number],
						name = result[Users.name],
						role = result[Users.role]
					)
				}
			}
			return user
		}

		suspend fun find(number: Int, name: String): User? {
			var user: User? = null
			newSuspendedTransaction {
				val result: ResultRow? = Users.selectAll().where {
					Users.number eq number
					Users.name eq name
				}.limit(1).singleOrNull()
				if (result !== null) {
					user = User(
						number = result[Users.number],
						name = result[Users.name],
						role = result[Users.role]
					)
				}
			}
			return user
		}

		suspend fun save(user: User): SaveStatus {
			var result = SaveStatus.FAILED
			newSuspendedTransaction {
				val row: ResultRow? = Users.selectAll().where {
					Users.number eq user.number
				}.limit(1).singleOrNull()
				if (row === null) {
					Users.insert {
						it[number] = user.number
						it[name] = user.name
						it[role] = user.role
					}
					result = SaveStatus.INSERT
				} else if (row[Users.name] == user.name) {
					Users.update({ Users.number eq user.number }) {
						it[role] = user.role
					}
					result = SaveStatus.UPDATE
				}
			}
			return result
		}

		suspend fun delete(user: User) {
			newSuspendedTransaction {
				Users.deleteWhere {
					number eq user.number
				}
			}
		}
	}
}