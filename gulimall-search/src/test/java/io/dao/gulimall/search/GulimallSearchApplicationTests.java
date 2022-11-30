package io.dao.gulimall.search;

import com.alibaba.fastjson.JSON;
import io.dao.gulimall.search.config.GulimallElasticSearchConfig;
import lombok.Data;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Map;

@SpringBootTest
class GulimallSearchApplicationTests {

	@Autowired
	private RestHighLevelClient client;

	@Test
	void searchData() throws IOException {
		// 创建检索请求
		SearchRequest request = new SearchRequest();
		// 指定索引
		request.indices("bank");
		// 指定DSL，检索条件
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		// 构造检索条件
		sourceBuilder.query(QueryBuilders.matchQuery("address", "mill"));
//		sourceBuilder.from();
//		sourceBuilder.size();

		TermsAggregationBuilder ageAgg = AggregationBuilders.terms("ageAgg").field("age").size(10);
		sourceBuilder.aggregation(ageAgg);

		AvgAggregationBuilder balanceAvg = AggregationBuilders.avg("balanceAvg").field("balance");
		sourceBuilder.aggregation(balanceAvg);

		System.out.println("检索条件" + sourceBuilder.toString());
		request.source(sourceBuilder);

		// 执行操作
		SearchResponse response = client.search(request, GulimallElasticSearchConfig.COMMON_OPTIONS);
		// 分析结果
		Map map = JSON.parseObject(response.toString(), Map.class);

		SearchHits hits = response.getHits();
		SearchHit[] searchHits = hits.getHits();
		for (SearchHit searchHit: searchHits) {
//			searchHit.getSourceAsMap()
//			searchHit.getSourceAsString()
		}

		Aggregations aggregations = response.getAggregations();
		for (Aggregation aggregation: aggregations) {
			// ...
		}

		Terms ageAggResp = aggregations.get("ageAgg");
		for (Terms.Bucket bucket : ageAggResp.getBuckets()) {
			// bucket.getAsString()
		}

		Avg balanceAvgResp = aggregations.get("balanceAvg");
//		balanceAvgResp.getValue()

	}

	@Test
	void indexData() throws IOException {
		IndexRequest request = new IndexRequest("users");
		request.id("1");
//		request.source("userName", "zhangsan", "age", 18, "gender", "男");
		User user = new User();
		user.setAge(18);
		user.setGender("男");
		user.setUserName("zhangsan");
		String jsonString = JSON.toJSONString(user);
		request.source(jsonString, XContentType.JSON);	// 要保存的内容
		// 执行操作
		IndexResponse response = client.index(request, GulimallElasticSearchConfig.COMMON_OPTIONS);
		// 响应数据
		System.out.println(response);
	}

	@Data
	class User {
		private String userName;
		private String gender;
		private Integer age;
	}

	@Test
	void contextLoads() {
		System.out.println(client);
	}

}
