package kr.foundcake.role_auth.database

import com.mysql.cj.jdbc.Driver
import kr.foundcake.role_auth.dto.UserDto
import kr.foundcake.role_auth.entity.User
import kr.foundcake.role_auth.table.Users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
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

		suspend fun find(serverId: Long, number: Int): User? {
			var user: User? = null
			newSuspendedTransaction {
				user = User.find {
					Users.serverId eq serverId
					Users.number eq number
				}.limit(1).singleOrNull()
			}
			return user
		}

		suspend fun find(serverId: Long, number: Int, name: String): User? {
			var user: User? = null
			newSuspendedTransaction {
				user = User.find {
					Users.serverId eq serverId
					Users.number eq number
					Users.name eq name
				}.limit(1).singleOrNull()
			}
			return user
		}

		suspend fun save(dto: UserDto): SaveResult {
			var result = SaveResult.FAILED
			newSuspendedTransaction {
				var user: User? = User.find {
					Users.serverId eq dto.serverId
					Users.number eq dto.number
					Users.name eq dto.name
				}.limit(1).singleOrNull()
				if (user === null) {
					user = User.new {
						serverId = dto.serverId
						number = dto.number
						name = dto.name
						role = dto.role
					}
					result = SaveResult.INSERT.setUser(user)
				} else if (user.name == dto.name) {
					user.role = dto.role
					result = SaveResult.UPDATE.setUser(user)
				}
			}
			return result
		}

		suspend fun delete(user: User) {
			newSuspendedTransaction {
				user.delete()
			}
		}
	}
}