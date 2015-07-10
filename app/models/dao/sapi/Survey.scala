package models.dao.sapi

import org.apache.commons.lang3.StringUtils

/**
 * Created by tashdid.khan on 7/10/2015.
 */
case class Survey(id: String = null, lineItems: Map[Category, List[LineItem]] = Map[Category, List[LineItem]](),  name :String = StringUtils.EMPTY, sourceUser: User = null, targetUser: User = null) extends AbstractModel
case class LineItem (id: String = null, minRange: Int =0,maxRange: Int =0, question: String, score: Int = 0)
case class Category(categoryname: String)